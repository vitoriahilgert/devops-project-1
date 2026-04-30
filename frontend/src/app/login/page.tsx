"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/context/auth-context";
import { ApiError } from "@/lib/api";
import { t } from "@/lib/strings";

export default function LoginPage() {
  const router = useRouter();
  const { login, token, ready } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (ready && token) router.replace("/");
  }, [ready, token, router]);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(email.trim(), password);
      router.replace("/");
    } catch (err) {
      if (err instanceof ApiError) {
        try {
          const j = JSON.parse(err.body) as { message?: string };
          setError(j.message ?? err.body);
        } catch {
          setError(err.body || "Falha no login");
        }
      } else setError("Erro inesperado");
    } finally {
      setLoading(false);
    }
  }

  if (ready && token) return null;

  return (
    <div className="login-page-wrap">
      <div className="login-container">
        <h1>{t.login.title}</h1>
        {error ? <p className="error-message">{error}</p> : null}
        <form onSubmit={onSubmit}>
          <label htmlFor="email" style={{ display: "none" }}>
            E-mail
          </label>
          <input
            type="email"
            id="email"
            placeholder={t.login.emailPh}
            required
            autoComplete="username"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <label htmlFor="password" style={{ display: "none" }}>
            {t.login.passwordPh}
          </label>
          <input
            type="password"
            id="password"
            placeholder={t.login.passwordPh}
            required
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit" disabled={loading}>
            {loading ? "…" : t.login.title}
          </button>
        </form>
        <span className="home-link">
          {t.login.guest}{" "}
          <Link href="/">{t.login.guestLink}</Link>
        </span>
      </div>
    </div>
  );
}
