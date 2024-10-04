import { Injectable, signal, WritableSignal } from '@angular/core';

import type { User } from '../app.model';
import {RegisterResponse} from "../auth/auth.model";

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private user$: WritableSignal<User | null> = signal<User | null>(null);

  user = this.user$.asReadonly();

  setUser(response: RegisterResponse): void {
    const user: User = {
      email: response.email,
      id: response.id,
      is_active: response.is_active,
      is_superuser: response.is_superuser,
      is_verified: response.is_verified,
      location: response.location,
      name: response.name,
      phone_number: response.phone_number,
      prefers_phone_call: response.prefers_phone_call,
      prefers_telegram: response.prefers_telegram,
      surname: response.surname,
      login: response.login,
      created_on: response.created_on,
      last_modified_on: response.last_modified_on,
    };

    user.is_verified = true;

    this.user$.set(user);
  }

  isUserVerified(): boolean {
    return this.user$()?.is_verified || false;
  }

  clearUser(): void {
    this.user$.set(null);
  }
}
