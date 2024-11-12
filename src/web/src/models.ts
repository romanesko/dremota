export interface User {
  chatId: number;
  photoUrl: number;
  username: string;
  firstName: string;
  lastName: string;
  lastVisit: string;
  paidUntil: string;
  activePayment?: boolean;
  displayName: string;
  createdAt: string;
}

export interface Query {
  id: number;
  chatId: number;
  request: string;
  context: string;
  prefix: string;
  response?: string;
  createdAt: string;
  updatedAt: string;
}


export interface Payment {
  id:number,
  chatId: number,
  createdAt: string;
  amount: number;
  currency:string;
}


export interface Balance {
  id: number;
  chatId: number;
  createdAt: string;
  days: number;
  due: string;
  paymentId: number;
  payment: Payment;
  rationale: string;
}


export interface Settings {
  context: string;
  post: string;
  prefix: string;
  emptyBalanceMessage: string;

}



export interface Command {
  key: string;
  description: string;
  type: string;
  message: string;
  postMessage: string;
  showInMenu: boolean;
  enabled: boolean;
  system: boolean;
  isNew: boolean;
}


export interface Price {
  id: number;
  title: string;
  amount: number;
  currency: string;
  days: number;
  enabled: boolean;
  createdAt?: string;
}

export interface Feedback {
  id: number;
  userId: number;
  message: string;
  read: boolean;
  createdAt: string;
}

export interface Message {
  id?: number;
  senderId: number,
  receiverId: number,
  message: string,
  createdAt: string,

}

export interface Income {
  amount: number,
  currency: string,
}

export interface UserStat {

  totalRequests: number,
  totalIncome: Income[]
}


export interface UserInfo {
  user: User,
  stat: UserStat
}


export interface Referral {
  enabled: boolean,
  holderReceives: number,
  holderMessage: string,
  referrerReceives: number,
  referrerMessage: string,
}