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
import { RetirementCalculatorService, RetirementCalculationRequest, RetirementCalculationResponse } from '../services/retirement-calculator.service';
import { HttpErrorResponse } from '@angular/common/http';

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
  calculatorForm: FormGroup;
  calculationResult: RetirementCalculationResponse | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private calculatorService: RetirementCalculatorService,
    private snackBar: MatSnackBar
  ) {
    this.calculatorForm = this.fb.group({
      currentAge: ['', [Validators.required, Validators.min(18), Validators.max(100)]],
      retirementAge: ['', [Validators.required, Validators.min(18), Validators.max(100)]],
      interestRate: ['', [Validators.required, Validators.min(0), Validators.max(100)]],
      lifestyleType: ['simple', Validators.required]
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

  onSubmit() {
    if (this.calculatorForm.valid) {
      this.isLoading = true;
      
      // Format the request data
      const formValue = this.calculatorForm.value;
      const request: RetirementCalculationRequest = {
        currentAge: Number(formValue.currentAge),
        retirementAge: Number(formValue.retirementAge),
        interestRate: Number(formValue.interestRate),
        lifestyleType: formValue.lifestyleType
      };
      
      this.calculatorService.calculateRetirement(request).subscribe({
        next: (response) => {
          this.calculationResult = response;
          this.isLoading = false;
        },
        error: (error: HttpErrorResponse) => {
          this.isLoading = false;
          let errorMessage = 'Error calculating retirement savings.';
          
          if (error.error?.message) {
            errorMessage = error.error.message;
          } else if (error.error?.error) {
            errorMessage = error.error.error;
          }
          
          this.snackBar.open(errorMessage, 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
          console.error('Calculation error:', error);
        }
      });
    }
  }

  getErrorMessage(controlName: string): string {
    const control = this.calculatorForm.get(controlName);
    if (control?.hasError('required')) {
      return 'This field is required';
    }
    if (control?.hasError('min')) {
      return `Minimum value is ${control.errors?.['min'].min}`;
    }
    if (control?.hasError('max')) {
      return `Maximum value is ${control.errors?.['max'].max}`;
    }
    return '';
  }

  getAgeComparisonError(): string {
    if (this.calculatorForm.hasError('ageComparison')) {
      return 'Retirement age must be greater than current age';
    }
    return '';
  }
}
