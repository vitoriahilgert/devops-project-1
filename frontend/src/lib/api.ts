import { getApiBase } from "./config";

const TOKEN_KEY = "gametests_access_token";

export class ApiError extends Error {
  constructor(
    public status: number,
    public body: string
  ) {
    super(`HTTP ${status}`);
    this.name = "ApiError";
  }
}

export function getStoredToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(TOKEN_KEY);
}

export function setStoredToken(token: string | null): void {
  if (typeof window === "undefined") return;
  if (token) window.localStorage.setItem(TOKEN_KEY, token);
  else window.localStorage.removeItem(TOKEN_KEY);
}

function authHeader(token: string | null): HeadersInit {
  if (!token) return {};
  const value = token.startsWith("Bearer ") ? token : `Bearer ${token}`;
  return { Authorization: value };
}

export async function apiJson<T>(
  path: string,
  init: RequestInit & { token?: string | null } = {}
): Promise<T> {
  const { token = getStoredToken(), ...rest } = init;
  const headers = new Headers(rest.headers);
  if (!headers.has("Content-Type") && rest.body && !(rest.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }
  Object.entries(authHeader(token)).forEach(([k, v]) => headers.set(k, v));

  const res = await fetch(`${getApiBase()}${path.startsWith("/") ? path : `/${path}`}`, {
    ...rest,
    headers,
  });

  if (!res.ok) {
    const body = await res.text();
    throw new ApiError(res.status, body);
  }

  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}

export async function apiVoid(
  path: string,
  init: RequestInit & { token?: string | null } = {}
): Promise<void> {
  const { token = getStoredToken(), ...rest } = init;
  const headers = new Headers(rest.headers);
  Object.entries(authHeader(token)).forEach(([k, v]) => headers.set(k, v));
  if (rest.body && !(rest.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  const res = await fetch(`${getApiBase()}${path.startsWith("/") ? path : `/${path}`}`, {
    ...rest,
    headers,
  });
  if (!res.ok) {
    const body = await res.text();
    throw new ApiError(res.status, body);
  }
}

export async function apiMultipart<T>(
  path: string,
  formData: FormData,
  token: string | null
): Promise<T> {
  const headers = new Headers();
  const t = token ?? getStoredToken();
  if (t) {
    headers.set("Authorization", t.startsWith("Bearer ") ? t : `Bearer ${t}`);
  }
  const res = await fetch(`${getApiBase()}${path.startsWith("/") ? path : `/${path}`}`, {
    method: "POST",
    body: formData,
    headers,
  });
  if (!res.ok) {
    const body = await res.text();
    throw new ApiError(res.status, body);
  }
  if (res.status === 204) return undefined as T;
  return res.json() as Promise<T>;
}
