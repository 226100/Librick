import {Injectable} from "@angular/core";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {User} from "@app/shared/models/user.model";
import { Observable } from 'rxjs/Rx'
import {environment} from "@env/environment";
import "rxjs/add/operator/do";
import "rxjs/add/operator/catch";
import 'rxjs/add/operator/map';
import {isNullOrUndefined} from "util";
import {ErrorObservable} from "rxjs/observable/ErrorObservable";
import {Subject} from "rxjs/Subject";

@Injectable()
export class AuthenticationService {
  private tokenKey: string = "tokenKey";
  private loggedIn$ = new Subject<boolean>();
  private userKey: string = "userKey";

  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<boolean> {
    let body = {email: email, password: password};

    this.http.post(`api/login`, body, {responseType: 'text', observe: 'response'}).subscribe(
      (response: HttpResponse<string>) => {
        let token = this.getTokenFromResponse(response);

        if(token) {
          this.saveToken(token);
          this.loggedIn$.next(true);
        }
        else {
          throw new Error("Couldn't receive token. Response status - " + response.status);
        }
      },
      (error: any) => {
        if(error.status === 401) {
          this.loggedIn$.next(false);
        }
        else {
          throw error;
        }
      });

    return this.loggedIn$.asObservable();
  }

  logout() {
    this.removeToken();
  }

  getToken(): string {
    return localStorage.getItem(this.tokenKey);
  }

  getUser(): Observable<User> {
    let user$: Observable<User>;
    if(this.isUserSaved()) {
      user$ = Observable.of(this.retrieveUserFromLocalStorage());
    }
    else {
      user$ = this.retrieveUserByHttp();
    }

    return user$;
  }

  isAuthenticated(): boolean {
    return !isNullOrUndefined(localStorage.getItem(this.tokenKey));
  }

  private getTokenFromResponse(response: HttpResponse<string>): string {
    let token = response.headers.get('Authorization');
    token = token.replace("Bearer ", "");
    return token;
  }

  private saveToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  private removeToken() {
    localStorage.removeItem(this.tokenKey);
  }

  private retrieveUserFromLocalStorage(): User {
    return <User> (JSON.parse(localStorage.getItem(this.userKey)));
  }

  private isUserSaved(): boolean {
    return !isNullOrUndefined(this.retrieveUserFromLocalStorage());
  }

  private retrieveUserByHttp(): Observable<User> {
     return this.http.post<User>("api/user/secure/current", null).do(
       (user: User) => {
         this.saveUser(user);
       });
  }

  private saveUser(user: User) {
    localStorage.setItem(this.userKey, JSON.stringify(user));
  }
}

