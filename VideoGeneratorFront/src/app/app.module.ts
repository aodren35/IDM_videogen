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
  MatToolbarModule, MatTooltipModule
} from "@angular/material";
import {VgCoreModule} from "videogular2/core";
import {VgControlsModule} from "videogular2/controls";
import {VgBufferingModule} from "videogular2/buffering";
import {VgOverlayPlayModule} from "videogular2/overlay-play";


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    VgCoreModule,
    VgControlsModule,
    VgOverlayPlayModule,
    VgBufferingModule,
    NguCarouselModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatChipsModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    MatTooltipModule
  ],
  providers: [
    {provide: APP_CONFIG, useValue: AppConfig},
    VignettesService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
