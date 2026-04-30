"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { apiJson, getStoredToken, setStoredToken } from "@/lib/api";
import type { LoginResponse } from "@/lib/types";
import { tokenIsAdmin } from "@/lib/jwt";

const EMAIL_KEY = "aa2_login_email";

export function getStoredLoginEmail(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(EMAIL_KEY);
}

function setStoredLoginEmail(email: string | null) {
  if (typeof window === "undefined") return;
  if (email) window.localStorage.setItem(EMAIL_KEY, email);
  else window.localStorage.removeItem(EMAIL_KEY);
}

type AuthContextValue = {
  token: string | null;
  loginEmail: string | null;
  isAdmin: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  ready: boolean;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [loginEmail, setLoginEmail] = useState<string | null>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    setToken(getStoredToken());
    setLoginEmail(getStoredLoginEmail());
    setReady(true);
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const res = await apiJson<LoginResponse>("/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
      token: null,
    });
    setStoredToken(res.accessToken);
    setStoredLoginEmail(email.trim());
    setToken(res.accessToken);
    setLoginEmail(email.trim());
  }, []);

  const logout = useCallback(() => {
    setStoredToken(null);
    setStoredLoginEmail(null);
    setToken(null);
    setLoginEmail(null);
  }, []);

  const value = useMemo(
    () => ({
      token,
      loginEmail,
      isAdmin: tokenIsAdmin(token),
      login,
      logout,
      ready,
    }),
    [token, loginEmail, login, logout, ready]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
