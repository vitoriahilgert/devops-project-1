"use client";

import { FormEvent, Suspense, useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/context/auth-context";
import { apiJson, apiVoid, ApiError } from "@/lib/api";
import type { Project, Strategy, TestSession, User } from "@/lib/types";
import { t } from "@/lib/strings";

function labelStatus(s: string): string {
  if (s === "CREATED") return "CRIADO";
  if (s === "IN_PROGRESS") return "EM EXECUÇÃO";
  if (s === "FINISHED") return "FINALIZADO";
  return s;
}

function SessionsInner() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const projectIdFilter = searchParams.get("projectId");
  const { token, isAdmin, ready } = useAuth();
  const [sessions, setSessions] = useState<TestSession[] | null>(null);
  const [projects, setProjects] = useState<Project[]>([]);
  const [strategies, setStrategies] = useState<Strategy[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [modalNew, setModalNew] = useState(false);
  const [modalDel, setModalDel] = useState<string | null>(null);
  const [newProjectId, setNewProjectId] = useState("");
  const [newStrategyId, setNewStrategyId] = useState("");
  const [newDuration, setNewDuration] = useState(30);
  const [newDesc, setNewDesc] = useState("");

  const load = useCallback(async () => {
    if (!token) return;
    try {
      const q = projectIdFilter ? `?projectId=${projectIdFilter}` : "";
      const [sess, projs, strat] = await Promise.all([
        apiJson<TestSession[]>(`/test-sessions${q}`, { token }),
        apiJson<Project[]>("/project?filter=false", { token }),
        apiJson<Strategy[]>("/strategies", { token: null }),
      ]);
      setSessions(sess);
      setProjects(projs);
      setStrategies(strat);
      setNewProjectId((prev) => prev || projectIdFilter || projs[0]?.id || "");
      setNewStrategyId((prev) => prev || strat[0]?.id || "");
      setError(null);
    } catch (e) {
      if (e instanceof ApiError) setError(e.body);
      else setError("Erro");
    }
  }, [token, projectIdFilter]);

  useEffect(() => {
    if (!ready) return;
    if (!token) {
      router.replace("/login");
      return;
    }
    load();
  }, [ready, token, router, load]);

  useEffect(() => {
    if (!token || !isAdmin) return;
    (async () => {
      try {
        setUsers(await apiJson<User[]>("/users", { token }));
      } catch {
        setUsers([]);
      }
    })();
  }, [token, isAdmin]);

  const projMap = Object.fromEntries(projects.map((p) => [p.id, p.name]));
  const stratMap = Object.fromEntries(strategies.map((s) => [s.id, s.name]));
  const userMap = Object.fromEntries(users.map((u) => [u.id, u.name]));

  async function createSession(e: FormEvent) {
    e.preventDefault();
    if (!token || !newProjectId) return;
    try {
      await apiJson(`/test-sessions/${newProjectId}`, {
        method: "POST",
        token,
        body: JSON.stringify({
          duration: newDuration,
          strategyId: newStrategyId,
          description: newDesc,
        }),
      });
      setModalNew(false);
      setNewDesc("");
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  async function deleteSession(e: FormEvent) {
    e.preventDefault();
    if (!token || !modalDel) return;
    try {
      await apiVoid(`/test-sessions/${modalDel}`, { method: "DELETE", token });
      setModalDel(null);
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  if (!ready || !token) return null;
  if (sessions === null) return <p style={{ padding: 24, color: "#888" }}>Carregando…</p>;

  return (
    <>
      {error ? (
        <div className="alert-legacy-error" style={{ margin: "0 5% 12px" }}>
          {error}
        </div>
      ) : null}
      <div className="legacy-container">
        <h1>{t.sessions.pageTitle}</h1>
        {projectIdFilter ? (
          <p style={{ color: "#aaa" }}>
            Filtrando pelo projeto: <strong>{projMap[projectIdFilter] ?? projectIdFilter}</strong> —{" "}
            <Link href="/sessions" style={{ color: "#00BFFF" }}>
              ver todas
            </Link>
          </p>
        ) : null}
        <button type="button" className="btn-create" onClick={() => setModalNew(true)}>
          {t.sessions.newBtn}
        </button>
        <table>
          <thead>
            <tr>
              <th>{t.sessions.tableProject}</th>
              <th>{t.sessions.tableTester}</th>
              <th>{t.sessions.tableStrategy}</th>
              <th>{t.sessions.tableStatus}</th>
              <th>{t.sessions.tableActions}</th>
            </tr>
          </thead>
          <tbody>
            {sessions.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ color: "#888" }}>
                  Nenhuma sessão.
                </td>
              </tr>
            ) : (
              sessions.map((s) => (
                <tr key={s.id}>
                  <td>{projMap[s.projectId] ?? s.projectId}</td>
                  <td>{userMap[s.testerId] ?? s.testerId}</td>
                  <td>{stratMap[s.strategyId] ?? s.strategyId}</td>
                  <td>{labelStatus(s.status)}</td>
                  <td className="actions">
                    <Link href={`/sessions/detail?id=${encodeURIComponent(s.id)}`} title={t.sessions.details} className="btn-action">
                      <i className="fas fa-eye" />
                    </Link>
                    {isAdmin ? (
                      <button
                        type="button"
                        title={t.sessions.delete}
                        className="btn-action"
                        onClick={() => setModalDel(s.id)}
                      >
                        <i className="fas fa-trash" />
                      </button>
                    ) : null}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className={modalNew ? "modal show" : "modal"}>
        <div className="modal-content">
          <span className="close" onClick={() => setModalNew(false)}>
            &times;
          </span>
          <h2>Nova sessão</h2>
          <form onSubmit={createSession}>
            <label>Projeto</label>
            <select value={newProjectId} onChange={(e) => setNewProjectId(e.target.value)} required>
              {projects.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name}
                </option>
              ))}
            </select>
            <label>Estratégia</label>
            <select value={newStrategyId} onChange={(e) => setNewStrategyId(e.target.value)} required>
              {strategies.map((st) => (
                <option key={st.id} value={st.id}>
                  {st.name}
                </option>
              ))}
            </select>
            <label>Tempo (min)</label>
            <input
              type="number"
              min={1}
              value={newDuration}
              onChange={(e) => setNewDuration(Number(e.target.value))}
            />
            <label>Descrição</label>
            <textarea value={newDesc} onChange={(e) => setNewDesc(e.target.value)} required rows={3} />
            <div style={{ marginTop: 16 }}>
              <input type="submit" value="Salvar" />
            </div>
          </form>
        </div>
      </div>

      <div className={modalDel ? "modal show" : "modal"}>
        <div className="modal-content">
          <span className="close" onClick={() => setModalDel(null)}>
            &times;
          </span>
          <h2>Excluir sessão?</h2>
          <form onSubmit={deleteSession}>
            <input type="submit" value="Confirmar" style={{ background: "#dc143c" }} />
            <button type="button" onClick={() => setModalDel(null)}>
              Cancelar
            </button>
          </form>
        </div>
      </div>
    </>
  );
}

export default function SessionsPage() {
  return (
    <Suspense fallback={<p style={{ padding: 24 }}>Carregando…</p>}>
      <SessionsInner />
    </Suspense>
  );
}
