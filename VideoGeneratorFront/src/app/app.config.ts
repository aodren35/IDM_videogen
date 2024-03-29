import { InjectionToken } from "@angular/core";

export let APP_CONFIG = new InjectionToken("app.config");

 export const baseUrl = "http://localhost:3000/api/v1/generator/";
 export const baseStaticUrl = "http://localhost:3000";


export interface IAppConfig {
  cacheId: string;
  cacheTw: string;
  cacheLg: string;
  color: {
    bleu: string,
    rouge: string,
    vert: string,
    jaune: string
  };
}

export interface IAppUrl {
  allVignettes: string;
  postVideogen: string;
  getVariante: string;
  getVariante2: string;
  getGif: string;
}
export const AppConfig: IAppConfig = {
  cacheId: "currentUserMobSat",
  cacheTw: "currentTwUser",
  cacheLg: "currentLgValue",
  color: {
    bleu: '#45C2E6',
    rouge: '#BA282B',
    vert: '#87C23F',
    jaune: '#FAAE17'
  }
};

export const AppUrl: IAppUrl = {
  allVignettes: baseUrl + "vignettes",
  postVideogen: baseUrl + "",
  getVariante: baseUrl + "variante",
  getVariante2: baseUrl + "variante2",
  getGif: baseStaticUrl + "/gif/"
};

export function getToken(): string {
  const currentUser = JSON.parse(localStorage.getItem('currentUserMobSat'));
  const token = currentUser && currentUser.token;
  return token;
}

export function refreshToken(response): boolean  {
  let token =  response.token;
  if (token) {
    // get stored if exists
    let temp = sessionStorage.getItem('currentUserMobSat');
    let tempLogin;
    if (temp) { tempLogin = JSON.parse(temp).login;
    } else {
      tempLogin = "";
    }
    // store username and jwt token in local storage to keep user logged in between page refreshes
    sessionStorage.setItem('currentUserMobSat', JSON.stringify({ login: tempLogin, token: token }));

    return true;
  } else {
    return false;
  }
}
