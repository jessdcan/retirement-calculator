import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RetirementCalculatorService, RetirementCalculationRequest, RetirementCalculationResponse } from '../services/retirement-calculator.service';

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
    MatProgressSpinnerModule
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
    });
  }

  onSubmit() {
    if (this.calculatorForm.valid) {
      this.isLoading = true;
      const request: RetirementCalculationRequest = this.calculatorForm.value;
      
      this.calculatorService.calculateRetirement(request).subscribe({
        next: (response) => {
          this.calculationResult = response;
          this.isLoading = false;
        },
        error: (error) => {
          this.isLoading = false;
          this.snackBar.open('Error calculating retirement savings. Please try again.', 'Close', {
            duration: 5000
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
}
