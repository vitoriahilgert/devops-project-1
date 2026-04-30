"use client";

import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/auth-context";
import { apiJson, apiVoid, ApiError } from "@/lib/api";
import { tokenSubjectId } from "@/lib/jwt";
import type { Project, User } from "@/lib/types";
import { t } from "@/lib/strings";

function formatDt(iso: string): string {
  try {
    const d = new Date(iso);
    return new Intl.DateTimeFormat("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(d);
  } catch {
    return iso;
  }
}

export default function ProjectsPage() {
  const router = useRouter();
  const { token, isAdmin, ready } = useAuth();
  const [projects, setProjects] = useState<Project[] | null>(null);
  const [testers, setTesters] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [order, setOrder] = useState<"nome" | "data">("nome");
  const [filter, setFilter] = useState<"todos" | "meus">("todos");
  const [modal, setModal] = useState<"none" | "edit" | "delete">("none");
  const [editId, setEditId] = useState<string | null>(null);
  const [deleteId, setDeleteId] = useState<string | null>(null);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [memberIds, setMemberIds] = useState<string[]>([]);

  const myId = tokenSubjectId(token);

  const load = useCallback(async () => {
    if (!token) return;
    try {
      const list = await apiJson<Project[]>("/project?filter=false", { token });
      setProjects(list);
      setError(null);
    } catch (e) {
      if (e instanceof ApiError) setError(e.body);
      else setError("Erro ao carregar");
    }
  }, [token]);

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
        const list = await apiJson<User[]>("/users/testers", { token });
        setTesters(list);
      } catch {
        setTesters([]);
      }
    })();
  }, [token, isAdmin]);

  const sortedFiltered = useMemo(() => {
    if (!projects) return [];
    let rows = [...projects];
    if (order === "nome") {
      rows.sort((a, b) => a.name.localeCompare(b.name, "pt-BR"));
    } else {
      rows.sort((a, b) => (a.creationDateTime < b.creationDateTime ? 1 : -1));
    }
    if (!isAdmin && filter === "meus" && myId) {
      rows = rows.filter((p) => p.allowedMembers?.some((m) => m.id === myId));
    }
    return rows;
  }, [projects, order, filter, isAdmin, myId]);

  function openNew() {
    setEditId(null);
    setName("");
    setDescription("");
    setMemberIds([]);
    setModal("edit");
  }

  function openEdit(p: Project) {
    setEditId(p.id);
    setName(p.name);
    setDescription(p.description);
    setMemberIds((p.allowedMembers ?? []).map((m) => m.id));
    setModal("edit");
  }

  function toggleMember(id: string) {
    setMemberIds((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
  }

  async function saveProject(e: FormEvent) {
    e.preventDefault();
    if (!token) return;
    try {
      if (editId) {
        await apiJson(`/project/${editId}`, {
          method: "PUT",
          token,
          body: JSON.stringify({
            name,
            description,
            allowedMembersIds: memberIds,
          }),
        });
      } else {
        await apiJson("/project", {
          method: "POST",
          token,
          body: JSON.stringify({
            name,
            description,
            allowedMembersIds: memberIds,
          }),
        });
      }
      setModal("none");
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  async function confirmDelete(e: FormEvent) {
    e.preventDefault();
    if (!token || !deleteId) return;
    try {
      await apiVoid(`/project/${deleteId}`, { method: "DELETE", token });
      setModal("none");
      setDeleteId(null);
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  if (!ready || !token) return null;
  if (projects === null) return <p style={{ padding: 24, color: "#888" }}>Carregando…</p>;

  return (
    <>
      {error ? (
        <div className="alert-legacy-error" style={{ margin: "0 5% 20px" }}>
          {error}
        </div>
      ) : null}
      <div className="legacy-container">
        <h1>{t.projects.pageTitle}</h1>
        <div className="controls-container">
          <div>
            <label htmlFor="ordenar">{t.projects.orderBy}</label>
            <select id="ordenar" value={order} onChange={(e) => setOrder(e.target.value as "nome" | "data")}>
              <option value="nome">{t.projects.orderName}</option>
              <option value="data">{t.projects.orderDate}</option>
            </select>
          </div>
          {!isAdmin ? (
            <div>
              <label htmlFor="filtrar">{t.projects.filterBy}</label>
              <select id="filtrar" value={filter} onChange={(e) => setFilter(e.target.value as "todos" | "meus")}>
                <option value="todos">{t.projects.filterAll}</option>
                <option value="meus">{t.projects.filterMine}</option>
              </select>
            </div>
          ) : null}
        </div>
        {isAdmin ? (
          <button type="button" onClick={openNew}>
            {t.projects.newBtn}
          </button>
        ) : null}

        <table id="tabelaProjetos">
          <thead>
            <tr>
              <th>{t.projects.tableName}</th>
              <th>{t.projects.tableDesc}</th>
              <th>{t.projects.tableDate}</th>
              <th>{t.projects.tableAction}</th>
            </tr>
          </thead>
          <tbody>
            {sortedFiltered.map((p) => (
              <tr
                key={p.id}
                data-allowed-member-ids={(p.allowedMembers ?? []).map((m) => m.id).join(",")}
                data-user-id={myId ?? ""}
              >
                <td>{p.name}</td>
                <td>{p.description}</td>
                <td>{formatDt(p.creationDateTime)}</td>
                <td className="actions">
                  {isAdmin ? (
                    <>
                      <button
                        type="button"
                        title={t.projects.edit}
                        className="btn-action"
                        onClick={() => openEdit(p)}
                      >
                        <i className="fas fa-pen" />
                      </button>
                      <button
                        type="button"
                        title={t.projects.delete}
                        className="btn-action"
                        onClick={() => {
                          setDeleteId(p.id);
                          setModal("delete");
                        }}
                      >
                        <i className="fas fa-trash" />
                      </button>
                    </>
                  ) : null}
                  <Link href={`/sessions?projectId=${p.id}`} title={t.projects.seeSessions} className="btn-action">
                    <i className="fas fa-clipboard-list" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div id="modalProjeto" className={modal === "edit" ? "modal show" : "modal"}>
        <div className="modal-content">
          <span className="close" onClick={() => setModal("none")}>
            &times;
          </span>
          <h2>{editId ? t.projects.modalEdit : t.projects.modalNew}</h2>
          <form onSubmit={saveProject}>
            <input type="hidden" value={editId ?? ""} readOnly />
            <label>{t.projects.name}</label>
            <input value={name} onChange={(e) => setName(e.target.value)} required />
            <label>{t.projects.description}</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} required rows={4} />
            <label>{t.projects.members}</label>
            <div id="allowedMembersCheckboxes" style={{ display: "flex", flexWrap: "wrap", gap: 10 }}>
              {testers.map((u) => (
                <label key={u.id} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                  <input
                    type="checkbox"
                    checked={memberIds.includes(u.id)}
                    onChange={() => toggleMember(u.id)}
                  />
                  {u.name} ({u.email})
                </label>
              ))}
            </div>
            <div id="cadastroSubmitContainer">
              <input type="submit" id="cadastroSubmit" value={t.projects.save} />
            </div>
          </form>
        </div>
      </div>

      <div id="modalDeletar" className={modal === "delete" ? "modal show" : "modal"}>
        <div className="modal-content">
          <span className="close" onClick={() => setModal("none")}>
            &times;
          </span>
          <h2>{t.projects.deleteTitle}</h2>
          <p>{t.projects.deleteDesc}</p>
          <form id="formDeletar" onSubmit={confirmDelete}>
            <input type="submit" value={t.projects.deleteConfirm} />
            <button type="button" onClick={() => setModal("none")}>
              {t.projects.deleteCancel}
            </button>
          </form>
        </div>
      </div>
    </>
  );
}

