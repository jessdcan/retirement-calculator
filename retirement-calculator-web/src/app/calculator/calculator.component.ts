import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RetirementCalculatorService } from '../services/retirement-calculator.service';
import { RetirementCalculationRequest, LIFESTYLE_TYPES } from '../models/retirement-request.model';
import { RetirementCalculationResponse } from '../models/retirement-response.model';

@Component({
  selector: 'app-calculator',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.css']
})
export class CalculatorComponent {
  calculatorForm!: FormGroup;
  calculationResult: RetirementCalculationResponse | null = null;
  isLoading = false;
  readonly lifestyleTypes = LIFESTYLE_TYPES;

  constructor(
    private fb: FormBuilder,
    private calculatorService: RetirementCalculatorService,
    private snackBar: MatSnackBar
  ) {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.calculatorForm = this.fb.group({
      currentAge: ['', [Validators.required, Validators.min(18), Validators.max(100)]],
      retirementAge: ['', [Validators.required, Validators.min(18), Validators.max(100)]],
      lifestyleType: [LIFESTYLE_TYPES.SIMPLE, Validators.required]
    }, { validators: this.ageComparisonValidator });
  }

  // Custom validator to ensure retirement age is greater than current age
  private ageComparisonValidator(group: AbstractControl): ValidationErrors | null {
    const currentAge = group.get('currentAge')?.value;
    const retirementAge = group.get('retirementAge')?.value;

    if (currentAge && retirementAge && currentAge >= retirementAge) {
      return { ageComparison: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.calculatorForm.valid) {
      this.isLoading = true;
      const request: RetirementCalculationRequest = this.prepareRequest();
      
      this.calculatorService.calculateRetirement(request).subscribe({
        next: (response) => {
          this.calculationResult = response;
          this.isLoading = false;
        },
        error: (error) => {
          this.isLoading = false;
          this.showError(error.message);
        }
      });
    }
  }

  private prepareRequest(): RetirementCalculationRequest {
    const formValue = this.calculatorForm.value;
    return {
      currentAge: Number(formValue.currentAge),
      retirementAge: Number(formValue.retirementAge),
      lifestyleType: formValue.lifestyleType
    };
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  getErrorMessage(controlName: string): string {
    const control = this.calculatorForm.get(controlName);
    if (!control) return '';

    if (control.hasError('required')) {
      return 'This field is required';
    }
    if (control.hasError('min')) {
      return `Minimum value is ${control.errors?.['min'].min}`;
    }
    if (control.hasError('max')) {
      return `Maximum value is ${control.errors?.['max'].max}`;
    }
    return '';
  }

  getAgeComparisonError(): string {
    return this.calculatorForm.hasError('ageComparison') 
      ? 'Retirement age must be greater than current age' 
      : '';
  }
}
