import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import * as myGlobals from '../app.config';
import 'rxjs/Rx';
import {ResponseContentType} from "@angular/http";

@Injectable()
export class VignettesService {

  vignettes: {url: string, name: string, selected: boolean}[];
  fileToUpload: File = null;


  constructor(private http: HttpClient) {
    this.vignettes = [];
  }

  public generate(listSelected: {url: string, name: string, selected: boolean}[]) {

  }

  public postFile(): Observable<any> {
    const headers = new HttpHeaders({'Cache-Control': 'no-cache'});
    const formData: FormData = new FormData();
    formData.append('fileKey', this.fileToUpload, this.fileToUpload.name);
    return this.http.post(
      myGlobals.AppUrl.postVideogen, formData, { headers: headers })
      .map((response) => {
      return true;
    }).catch((e) => {return null;});
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
        for (const r of response) {
         const n = {url: r.url, name: r.name, selected: false};
         this.vignettes.push(n);
        }
        return this.vignettes;
      },
      (error => {
        return null;
      })
    );
  }

  public getVariante(): Observable<any> {
    const headers = new HttpHeaders({'Cache-Control': 'no-cache'});
    const options = {
      headers: headers,
      params: {
      }
    };
    return this.http.get(
      myGlobals.AppUrl.getVariante,
      options
    ).timeout(50000).map(
      (response: any) => {
        return response.url;
      },
      (error => {
        return null;
      })
    );
  }

  public getGif(url: string): Observable<Blob> {
    const headers = new HttpHeaders({'Cache-Control': 'no-cache'});
    const options = {
      headers: headers,
      params: {
      }
    };
    return this.http.get(
      myGlobals.AppUrl.getGif  + url + '.gif',
      {
        observe: 'response',
        responseType: 'blob'
      }
    ).timeout(50000).map(
      (response: any) => {
        return new Blob([response.body], { type: 'image/gif' });
      },
      (error => {
        return null;
      })
    );
  }
}
