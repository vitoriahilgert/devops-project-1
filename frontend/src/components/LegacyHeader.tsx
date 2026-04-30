"use client";

import Link from "next/link";
import { useAuth } from "@/context/auth-context";
import { t } from "@/lib/strings";

export function LegacyHeader() {
  const { token, isAdmin, logout, ready, loginEmail } = useAuth();

  if (!ready) return null;

  const greet = loginEmail ? `Olá, ${loginEmail}!` : "Olá!";

  return (
    <header>
      {token ? (
        <div className="page-header">
          <div className="logo-title">
            <h1>{isAdmin ? t.header.adminTitle : t.header.testerTitle}</h1>
            <p>{isAdmin ? t.header.adminSubtitle : t.header.testerSubtitle}</p>
          </div>
          <div className="user-info">
            <span style={{ marginLeft: 10 }}>{greet}</span>
            <button type="button" className="logout-button" onClick={() => logout()}>
              {t.header.logout}
            </button>
          </div>
        </div>
      ) : (
        <div className="page-header public-header">
          <div className="logo-title">
            <h1>{t.header.title}</h1>
            <p>{t.header.subtitle}</p>
          </div>
          <div className="user-info">
            <div className="auth-links">
              <Link href="/login">
                <button type="button">Login</button>
              </Link>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
