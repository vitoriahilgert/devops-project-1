"use client";

import Link from "next/link";
import { useAuth } from "@/context/auth-context";
import { t } from "@/lib/strings";

export default function HomePage() {
  const { token, isAdmin, ready } = useAuth();

  if (!ready) return null;

  return (
    <main>
      {!token && (
        <div className="page-content-container">
          <h2 className="welcome-title">{t.home.welcome}</h2>
          <p className="welcome-subtitle">{t.home.welcomeSubtitle}</p>
          <div className="management-card" style={{ maxWidth: 500, textAlign: "center" }}>
            <h3>{t.home.strategyCardTitle}</h3>
            <p>{t.home.strategyCardDesc}</p>
            <div className="actions">
              <Link href="/strategies" className="button" style={{ color: "black", width: "fit-content", marginInline: "auto" }}>
                {t.home.strategyView}
              </Link>
            </div>
          </div>
        </div>
      )}

      {token && isAdmin && (
        <div className="dashboard-container">
          <div className="welcome-message">
            <h2>{t.home.adminWelcome}</h2>
            <p>{t.home.adminSubtitle}</p>
          </div>
          <div className="management-sections">
            <div className="management-card">
              <h3>{t.home.projectTitle}</h3>
              <p>{t.home.projectDesc}</p>
              <div className="actions">
                <Link href="/projects" className="button secondary">
                  {t.home.projectBtn}
                </Link>
              </div>
            </div>
            <div className="management-card">
              <h3>{t.home.strategyAdminTitle}</h3>
              <p>{t.home.strategyAdminDesc}</p>
              <div className="actions">
                <Link href="/strategies" className="button secondary">
                  {t.home.strategyListBtn}
                </Link>
                <Link href="/strategies/create" className="button">
                  {t.home.strategyCreate}
                </Link>
              </div>
            </div>
            <div className="management-card">
              <h3>{t.home.usersTitle}</h3>
              <p>{t.home.usersDesc}</p>
              <div className="actions">
                <Link href="/admin/users/admins" className="button secondary">
                  {t.home.usersAdmins}
                </Link>
                <Link href="/admin/users/testers" className="button secondary">
                  {t.home.usersTesters}
                </Link>
              </div>
            </div>
            <div className="management-card">
              <h3>{t.home.sessionsTitle}</h3>
              <p>{t.home.sessionsDesc}</p>
              <div className="actions">
                <Link href="/sessions" className="button secondary">
                  {t.home.sessionsBtn}
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {token && !isAdmin && (
        <div className="dashboard-container">
          <div className="welcome-message">
            <h2>{t.home.testerWelcome}</h2>
            <p>{t.home.testerSubtitle}</p>
            <div className="quick-action">
              <Link href="/sessions" className="button-primary">
                {t.home.testerSessionsBtn}
              </Link>
            </div>
          </div>
          <div className="dashboard-grid">
            <div className="main-content-area">
              <div className="management-card">
                <h3>{t.home.sessionsTitle}</h3>
                <p>{t.home.sessionsDesc}</p>
                <div className="actions">
                  <Link href="/sessions" className="button secondary">
                    {t.home.sessionsBtn}
                  </Link>
                </div>
              </div>
              <div className="content-card">
                <div className="management-card">
                  <h3>{t.home.projectViewTitle}</h3>
                  <p>{t.home.projectViewDesc}</p>
                  <div className="actions">
                    <Link href="/projects" className="button secondary">
                      {t.home.projectViewBtn}
                    </Link>
                  </div>
                </div>
              </div>
            </div>
            <div className="sidebar-area">
              <div className="management-card">
                <h3>{t.home.strategyCardTitle}</h3>
                <p>{t.home.strategyCardDesc}</p>
                <div className="actions">
                  <Link href="/strategies" className="button secondary">
                    {t.home.strategyView}
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}
