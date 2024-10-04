import { FormControl } from '@angular/forms';

export interface LoginFormData {
  email: string;
  password: string;
}

export interface RegisterFormData extends LoginFormData {
  login: string;
  name: string;
  surname: string;
  location: string;
  confirmPassword: string;
}

export type NestedFormInterface<T extends LoginFormData> = {
  [key in keyof T]: FormControl<string>;
};

export type SignUpData = Omit<RegisterFormData, 'confirmPassword'>;

export interface LoginRequestData {
  username: string;
  password: string;
}

export interface LinkModalData {
  email: string;
}

export interface RegisterResponse {
  email: string;
  id: number;
  is_active: boolean;
  is_superuser: boolean;
  is_verified: boolean;
  location: string;
  name: string;
  phone_number: string;
  prefers_phone_call: boolean;
  prefers_telegram: boolean;
  surname: string;
  login: string;
  created_on: string;
  last_modified_on: string;
}
