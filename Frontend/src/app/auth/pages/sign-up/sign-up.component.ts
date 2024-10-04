import {Component, inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators, FormBuilder} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {catchError, EMPTY, map, take, tap} from 'rxjs';

import {InputComponent} from '../../../shared/components/input/input.component';
import {ButtonComponent} from '../../../shared/components/button/button.component';
import {CheckboxComponent} from '../../../shared/components/checkbox/checkbox.component';
import {SelectComponent} from '../../../shared/components/select/select.component';
import {Router} from '@angular/router';
import {
  BUTTON_CONTENT,
  INPUT_LABELS,
  INPUT_TYPES, PLACEHOLDERS,
  ROUTING_LIST,
  TOASTER_MESSAGE,
  TOASTER_TYPE
} from '../../../app.config';
import type {LoginRequestData, SignUpData} from '../../auth.model';
import CITY_LIST from '../../../../city-db';
import {AuthApiService} from '../../services/auth-api.service';
import {UserService} from '../../../services/user.service';
import {ToasterService} from '../../../services/toaster.service';
import {TokenService} from "../../../services/token.service";

@Component({
  selector: 'pet-world-sign-up',
  standalone: true,
  imports: [ButtonComponent, InputComponent, RouterLink, CheckboxComponent, SelectComponent],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.scss',
})
export class SignUpComponent implements OnInit {
  readonly buttonContent = BUTTON_CONTENT;
  readonly inputLabels = INPUT_LABELS;
  readonly inputTypes = INPUT_TYPES;
  readonly routingList = ROUTING_LIST;
  readonly placeholders = PLACEHOLDERS;
  readonly cityList = CITY_LIST;

  private readonly fb = inject(FormBuilder);
  private readonly apiService = inject(AuthApiService);
  private readonly toasterService = inject(ToasterService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly tokenService = inject(TokenService);
  readonly confirmCheckboxControl = new FormControl(false);

  private authForm!: FormGroup;

  get login(): FormControl {
    return this.authForm.get('login') as FormControl;
  }

  get name(): FormControl {
    return this.authForm.get('name') as FormControl;
  }

  get surname(): FormControl {
    return this.authForm.get('surname') as FormControl;
  }

  get location(): FormControl {
    return this.authForm.get('location') as FormControl;
  }

  get email(): FormControl {
    return this.authForm.get('email') as FormControl;
  }

  get password(): FormControl {
    return this.authForm.get('password') as FormControl;
  }

  get confirmPassword(): FormControl {
    return this.authForm.get('confirmPassword') as FormControl;
  }

  get isFormValid(): boolean {
    return this.authForm.valid && (this.confirmCheckboxControl.value || false);
  }

  ngOnInit(): void {
    this.authForm = this.initForm();
  }

  signUp(): void {
    if (this.authForm.valid) {
      this.apiService
        .signUp(this.getRequestBody())
        .pipe(
          tap((response) => {
            this.userService.setUser(response);
            this.router.navigate([this.routingList.me]);

            const request: LoginRequestData = {
              username: this.login.value,
              password: this.password.value,
            }

            this.apiService
              .signIn(request)
              .pipe(
                tap(token => this.tokenService.setToken(token)),
                map(token => token.access_token)).subscribe();
          }),
          take(1),
          catchError((error: Error) => {
            console.error('Server error: ' + error);
            this.toasterService.show(TOASTER_TYPE.ERROR, TOASTER_MESSAGE.SERVER_ERROR);
            return EMPTY;
          }),
        )
        .subscribe();
    }
  }

  protected initForm(): FormGroup {
    return this.fb.group({
      email: ['', {validators: [Validators.required, Validators.email]}],
      password: ['', {validators: [Validators.required]}],
      login: ['', {validators: [Validators.required]}],
      name: ['', {validators: [Validators.required]}],
      surname: ['', {validators: [Validators.required]}],
      location: [null as unknown as string, {validators: [Validators.required]}],
      confirmPassword: ['', {validators: [Validators.required]}],
    });
  }

  private getRequestBody(): SignUpData {
    return {
      email: this.email.value,
      password: this.password.value,
      login: this.login.value,
      name: this.name.value,
      surname: this.surname.value,
      location: this.location.value,
    };
  }
}
