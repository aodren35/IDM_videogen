import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import * as myGlobals from '../app.config';
import 'rxjs/Rx';

@Injectable()
export class VignettesService {

  vignettes: string[];

  constructor(private http: HttpClient) {
    this.vignettes = [];
  }


  public getAllVignettes(): Observable<any> {
    const headers = new HttpHeaders({'Cache-Control': 'no-cache'});
    const options = {
      headers: headers,
      params: {
      }
    };
    return this.http.get(
      myGlobals.AppUrl.allVignettes,
      options
    ).timeout(5000).map(
      (response: any) => {
        this.vignettes = response;
        return response;
      },
      (error => {
        return null;
      })
    );
  }
}
