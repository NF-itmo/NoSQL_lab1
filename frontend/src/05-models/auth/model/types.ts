export type LoginRequest = {
  login: string;
  password: string;
};

export type RegisterRequest = LoginRequest & {
  inviteCode: string;
};
