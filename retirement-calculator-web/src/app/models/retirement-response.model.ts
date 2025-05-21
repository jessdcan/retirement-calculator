import { LifestyleType } from './retirement-request.model';

export interface RetirementCalculationResponse {
  currentAge: number;
  retirementAge: number;
  interestRate: number;
  lifestyleType: LifestyleType;
  totalRetirementSavings: number;
  monthlyDeposit: number;
  yearsToRetirement: number;
} 