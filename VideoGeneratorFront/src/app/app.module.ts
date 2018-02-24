import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import {VignettesService} from './shared/vignettes.service';
import {APP_CONFIG, AppConfig} from "./app.config";
import {HttpClientModule} from "@angular/common/http";
import {NguCarouselModule} from "@ngu/carousel";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {
  MatButtonModule, MatButtonToggleModule, MatChipsModule, MatIconModule, MatProgressSpinnerModule,
  MatToolbarModule
} from "@angular/material";


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NguCarouselModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatChipsModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatProgressSpinnerModule
  ],
  providers: [
    {provide: APP_CONFIG, useValue: AppConfig},
    VignettesService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
