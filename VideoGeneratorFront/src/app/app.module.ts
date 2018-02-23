import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import {VignettesService} from './shared/vignettes.service';
import {APP_CONFIG, AppConfig} from "./app.config";
import {HttpClientModule} from "@angular/common/http";
import {NguCarouselModule} from "@ngu/carousel";


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NguCarouselModule,
    HttpClientModule
  ],
  providers: [
    {provide: APP_CONFIG, useValue: AppConfig},
    VignettesService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
