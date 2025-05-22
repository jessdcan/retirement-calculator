export interface RetirementCalculationRequest {
  currentAge: number;
  retirementAge: number;
  lifestyleType: 'simple' | 'fancy';
  customInterestRate?: number;
}

export const LIFESTYLE_TYPES = {
  SIMPLE: 'simple',
  FANCY: 'fancy'
} as const;

export type LifestyleType = typeof LIFESTYLE_TYPES[keyof typeof LIFESTYLE_TYPES]; 