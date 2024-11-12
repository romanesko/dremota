import {auth_token, setToken} from "@/store/auth";
import type {
  Balance,
  Command,
  Feedback,
  Message,
  Payment,
  Price,
  Query,
  Referral,
  Settings,
  User,
  UserInfo
} from "@/models";
import {BASE_URL, log} from "@/lib/common";



class Api {

  private token: string = "";

  setToken(t: string) {
    this.token = t
  }

  private async get(url: string): Promise<any> {
    return this.request(url, 'GET', {})
  }

  private async post(url: string, params: any): Promise<any> {
    return this.request(url, 'POST', params)
  }
  private async put(url: string, params: any): Promise<any> {
    return this.request(url, 'PUT', params)
  }

  private  async request(url: string, method: 'GET' | 'POST' | 'PUT', params: any) {

    let body = undefined
    if (method === 'POST' || method === 'PUT') {
      body = JSON.stringify(params)
    }

    try {

      const options = {
        method: method,
        headers: {
          'Authorization': `Bearer ${this.token}`,
          'Content-Type': 'application/json'
        },
        body: body
      }


      const response = await fetch(BASE_URL + "/api/" + url, options);


      const json = await response.json();

      if(method==='GET'){
        log('GET', url,json)
      } else {
        log(method, url, body, json)
      }

      if (!response.ok) {
        if (response.status === 401) {
          setToken('');
          throw new Error('Unauthorized');
        }
        throw new Error(json.message);
      }

      setToken(this.token);

      return json
    } catch (e) {
      throw e;
    }
  }

  async getState() {
    return this.get('status');
  }

  async getUsers() : Promise<User[]>{
    return this.get('users');
  }

  async getQueries(): Promise<Query[]> {
    return this.get('queries');
  }

  getQueriesForUser(userId: number): Promise<Query[]> {
    return this.get('queries/' + userId)
  }

  getPaymentsForUser(userId: number): Promise<Payment[]> {
    return this.get('payments/' + userId)
  }

  getBalanceForUser(userId: number): Promise<Balance[]> {
    return this.get('balance/' + userId)
  }

  async addBalance(userId: number, days: any, rationale: any) {
    if (!days){
      throw new Error('Количество дней: параметр обязателен')
    }
    if (!rationale){
      throw new Error('Основание пополнения: параметр обязателен')
    }
    return this.post('balance/' + userId, {days, rationale})
  }

  getSettings(): Promise<Settings> {
    return this.get('settings')
  }

  saveSettings(settings: Settings) {
    return this.post('settings', settings)

  }

  getCommands(): Promise<Command[]> {
    return this.get('settings/commands')
  }

  updateCommand(command: Command) {
    return this.put('settings/commands', command)
  }

  addCommand(command: Command) {
    return this.post('settings/commands', command)
  }

  getCommandTypes() : Promise<{[key:string]:string}>{
    return this.get('settings/commands/types')
  }


  deleteCommand(command: Command) {
    return this.post('settings/commands/delete', command)
  }

  getPrice(): Promise<Price[]> {
    return this.get('settings/price')
  }

  updatePrice(price: Price) {
    return this.post('settings/price', price)
  }

  deletePrice(price: Price) {
    return this.post('settings/price/delete', price)
  }

  getMe(): Promise<User> {
    return this.get('users/me')
  }

  getFeedbackForUser(userId: number): Promise<Feedback[]> {
    return this.get('feedback/' + userId)
  }

  getUser(userId: number) {
    return this.get('users/' + userId)
  }

  markFeedbackRead(id: number) {
    return this.post('feedback/read',{id})

  }

  usersWithUnreadFeedback() : Promise<number[]> {
    return this.get('feedback/unread')
  }

  addMessage(msg: Message) {
    return this.post('messages', msg)
  }

  async getMessagesForUser(userId: number): Promise<Message[]> {
    return this.get('messages/' + userId)

  }

  getUserInfo(chatId: number): Promise<UserInfo> {
    return this.get('users/' + chatId + '/info')
  }

  getReferral() {
    return this.get('settings/referral')
  }

  saveReferral(referral: Referral) {
    return this.post('settings/referral', referral)
  }
}

const api = new Api();

auth_token.subscribe(token => {
  if (token) {
    api.setToken(token);
  }
});


export default api;