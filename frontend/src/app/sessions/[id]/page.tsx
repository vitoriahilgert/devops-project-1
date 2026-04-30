"use client";

import { FormEvent, useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/context/auth-context";
import { apiJson, ApiError } from "@/lib/api";
import { assetUrl } from "@/lib/config";
import type { Project, Strategy, TestSession } from "@/lib/types";
import { t } from "@/lib/strings";

function fmt(iso: string | null): string {
  if (!iso) return "";
  try {
    return new Intl.DateTimeFormat("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    }).format(new Date(iso));
  } catch {
    return iso;
  }
}

function statusClass(s: string): string {
  if (s === "CREATED") return "status-CREATED";
  if (s === "IN_PROGRESS") return "status-IN_PROGRESS";
  if (s === "FINISHED") return "status-FINISHED";
  return "";
}

export default function SessionDetailPage() {
  const params = useParams();
  const id = typeof params.id === "string" ? params.id : null;
  const router = useRouter();
  const { token, ready } = useAuth();
  const [session, setSession] = useState<TestSession | null | undefined>(undefined);
  const [projectName, setProjectName] = useState("");
  const [strategy, setStrategy] = useState<Strategy | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [bugText, setBugText] = useState("");

  const load = useCallback(async () => {
    if (!token || !id) return;
    try {
      const s = await apiJson<TestSession>(`/test-sessions/${id}`, { token });
      setSession(s);
      const proj = await apiJson<Project>(`/project/${s.projectId}`, { token });
      setProjectName(proj.name);
      const strat = await apiJson<Strategy>(`/strategies/${s.strategyId}`, { token: null });
      setStrategy(strat);
      setError(null);
    } catch (e) {
      setSession(null);
      if (e instanceof ApiError) setError(e.body);
      else setError("Não encontrado");
    }
  }, [token, id]);

  useEffect(() => {
    if (!ready) return;
    if (!token) {
      router.replace("/login");
      return;
    }
    if (id) load();
  }, [ready, token, id, router, load]);

  async function patchStatus() {
    if (!token || !id) return;
    try {
      await apiJson(`/test-sessions/status/${id}`, { method: "PATCH", token });
      await load();
    } catch (e) {
      if (e instanceof ApiError) setError(e.body);
    }
  }

  async function addBug(e: FormEvent) {
    e.preventDefault();
    if (!token || !id) return;
    try {
      await apiJson(`/test-sessions/add-bug/${id}`, {
        method: "PATCH",
        token,
        body: JSON.stringify({ bug: bugText }),
      });
      setBugText("");
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  if (!ready || !token || !id) return null;
  if (session === undefined) return <p style={{ padding: 24, color: "#888" }}>Carregando…</p>;
  if (session === null) {
    return (
      <div className="session-page-container">
        <p style={{ color: "#dc143c" }}>{error ?? "Erro"}</p>
        <Link href="/sessions" className="back-link">
          {t.sessionDetails.back}
        </Link>
      </div>
    );
  }

  const s = session;
  const finished = s.status === "FINISHED";

  return (
    <div className="session-page-container">
      <div className="session-page-header">
        <h1>{t.sessionDetails.title}</h1>
        <Link href="/sessions" className="back-link">
          {t.sessionDetails.back}
        </Link>
      </div>
      {error ? <p style={{ color: "#dc143c" }}>{error}</p> : null}

      <div className="detail-card">
        <h2>{t.sessionDetails.info}</h2>
        <p>
          <strong>{t.sessionDetails.id}</strong> <span>{s.id}</span>
        </p>
        <p>
          <strong>{t.sessionDetails.project}</strong> <span>{projectName}</span>
        </p>
        <p>
          <strong>Status:</strong>{" "}
          <span className={`status-badge ${statusClass(s.status)}`}>{s.status}</span>
        </p>
        <p>
          <strong>{t.sessionDetails.time}</strong> <span>{s.duration} minutos</span>
        </p>
        <p>
          <strong>{t.sessionDetails.description}</strong> <span>{s.description}</span>
        </p>
        <p>
          <strong>{t.sessionDetails.date}</strong> <span>{fmt(s.creationDateTime)}</span>
        </p>
        <p>
          <strong>{t.sessionDetails.start}</strong>{" "}
          <span>
            {s.startDateTime ? fmt(s.startDateTime) : <span className="status-PENDENTE">{t.sessionDetails.pending}</span>}
          </span>
        </p>
        <p>
          <strong>{t.sessionDetails.end}</strong>{" "}
          <span>
            {s.finishDateTime ? fmt(s.finishDateTime) : <span className="status-PENDENTE">{t.sessionDetails.pending}</span>}
          </span>
        </p>
      </div>

      {strategy ? (
        <div className="detail-card">
          <h2>{t.sessionDetails.strategy}</h2>
          <p>
            <strong>{t.sessionDetails.name}</strong> {strategy.name}
          </p>
          <p>{strategy.description}</p>
          {strategy.imageUrls?.length ? (
            <div className="estrategia-images">
              {strategy.imageUrls.map((u) => (
                // eslint-disable-next-line @next/next/no-img-element
                <img key={u} src={assetUrl(u)} alt="" />
              ))}
            </div>
          ) : null}
        </div>
      ) : null}

      <div className="detail-card">
        <h2>{t.sessionDetails.bugs}</h2>
        {s.bugs ? <pre className="current-description">{s.bugs}</pre> : <p style={{ color: "#888" }}>—</p>}
        {!finished ? (
          <form onSubmit={addBug}>
            <label>{t.sessionDetails.newBug}</label>
            <textarea className="bug-textarea" value={bugText} onChange={(e) => setBugText(e.target.value)} required />
            <button type="submit" className="button-bug">
              {t.sessionDetails.addBug}
            </button>
          </form>
        ) : null}
      </div>

      <div className="detail-card actions-section">
        <h2>{t.sessionDetails.actions}</h2>
        {finished ? (
          <p style={{ color: "#aaa", fontStyle: "italic" }}>{t.sessionDetails.completed}</p>
        ) : (
          <button type="button" className="button-action start-button" onClick={() => patchStatus()}>
            {s.status === "CREATED" ? t.sessionDetails.begin : t.sessionDetails.finish}
          </button>
        )}
      </div>
    </div>
  );
}
