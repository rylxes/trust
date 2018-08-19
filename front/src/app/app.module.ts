import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {MenuComponent} from './menu/menu.component';
import {AlertComponent} from './alert/alert.component';
import {AppRoutingModule} from './/app-routing.module';
import {RegisterComponent} from './register/register.component';
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {AlertService} from "./_services/alert.service";
import {Subject} from "rxjs/internal/Subject";
import {LoginComponent} from './login/login.component';
import {AuthServiceConfig, FacebookLoginProvider, SocialLoginModule} from "angular5-social-login";
import {environment} from "../environments/environment";

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    AlertComponent,
    RegisterComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    AppRoutingModule,
    SocialLoginModule,
    HttpClientModule
  ],
  providers: [
    Subject,
    AlertService,
    {
      provide: AuthServiceConfig,
      useFactory: getAuthServiceConfigs
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function getAuthServiceConfigs() {
  return new AuthServiceConfig(
    [
      {
        id: FacebookLoginProvider.PROVIDER_ID,
        provider: new FacebookLoginProvider(environment.social.facebook.appId)
      }
    ]
  );
}
