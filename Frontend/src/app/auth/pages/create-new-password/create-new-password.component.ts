import { Component, inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import { take } from 'rxjs';

import { ButtonComponent } from '../../../shared/components/button/button.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { BUTTON_CONTENT, INPUT_LABELS, INPUT_TYPES, PLACEHOLDERS } from '../../../app.config';
import { AuthApiService } from '../../services/auth-api.service';

@Component({
  selector: 'pet-world-confirm-password',
  standalone: true,
  imports: [ButtonComponent, InputComponent],
  templateUrl: './create-new-password.component.html',
  styleUrl: './create-new-password.component.scss',
})
export class CreateNewPasswordComponent implements OnInit {
  readonly buttonContent = BUTTON_CONTENT;
  readonly inputLabels = INPUT_LABELS;
  readonly placeholders = PLACEHOLDERS;
  readonly inputTypes = INPUT_TYPES;
  readonly password = new FormControl<string>('', Validators.required);
  readonly passwordDuplicate = new FormControl<string>('', Validators.required);

  private readonly apiService = inject(AuthApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private token: string | null = null;

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.token = params.get('token');
    });
  }

  get isPasswordInvalid(): boolean {
    return (
      this.password.invalid || this.passwordDuplicate.invalid || this.password.value !== this.passwordDuplicate.value
    );
  }

  onChangePassword(): void {
    if (!this.isPasswordInvalid && this.token) {
      this.apiService.createNewPassword(this.password.value!, this.token).pipe(take(1)).subscribe(() => {
        this.router.navigate(['/auth/sign-in']);
      });
    }
  }
}
