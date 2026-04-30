export type UserRole = "ADMIN" | "TESTER";

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
}

export interface ProjectMember {
  id: string;
  name: string;
  email: string;
}

export interface Project {
  id: string;
  name: string;
  description: string;
  creationDateTime: string;
  allowedMembers: ProjectMember[];
}

export interface Strategy {
  id: string;
  name: string;
  description: string;
  examples: string;
  tips: string;
  imageUrls: string[];
}

export type TestSessionStatus = "CREATED" | "IN_PROGRESS" | "FINISHED";

export interface TestSession {
  id: string;
  duration: number;
  description: string;
  status: TestSessionStatus;
  creationDateTime: string;
  startDateTime: string | null;
  finishDateTime: string | null;
  testerId: string;
  projectId: string;
  strategyId: string;
  bugs: string | null;
}

export interface LoginResponse {
  accessToken: string;
}
