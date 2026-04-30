"use client";

import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/auth-context";
import { apiJson, apiVoid, ApiError } from "@/lib/api";
import type { User, UserRole } from "@/lib/types";
import { t } from "@/lib/strings";

const copy = {
  ADMIN: {
    title: t.admins.pageTitle,
    newBtn: t.admins.newBtn,
    path: "/users/admins",
    empty: t.admins.pageTitle,
  },
  TESTER: {
    title: t.testers.pageTitle,
    newBtn: t.testers.newBtn,
    path: "/users/testers",
    empty: t.testers.pageTitle,
  },
} as const;

export function UserRoleManagement({ pageRole }: { pageRole: UserRole }) {
  const router = useRouter();
  const { token, isAdmin, ready } = useAuth();
  const [users, setUsers] = useState<User[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [sort, setSort] = useState<"nome" | "email">("nome");
  const [modal, setModal] = useState<"none" | "edit" | "delete">("none");
  const [editId, setEditId] = useState<string | null>(null);
  const [delId, setDelId] = useState<string | null>(null);
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [editRole, setEditRole] = useState<UserRole>(pageRole);

  const c = copy[pageRole];

  const load = useCallback(async () => {
    if (!token) return;
    try {
      const list = await apiJson<User[]>(c.path, { token });
      setUsers(list);
      setError(null);
    } catch (e) {
      if (e instanceof ApiError) setError(e.body);
      else setError("Erro");
    }
  }, [token, c.path]);

  useEffect(() => {
    if (!ready) return;
    if (!token) {
      router.replace("/login");
      return;
    }
    if (!isAdmin) {
      router.replace("/");
      return;
    }
    load();
  }, [ready, token, isAdmin, router, load]);

  const sorted = useMemo(() => {
    if (!users) return [];
    const u = [...users];
    const idx = sort === "nome" ? 0 : 1;
    u.sort((a, b) => {
      const av = idx === 0 ? a.name : a.email;
      const bv = idx === 0 ? b.name : b.email;
      return av.localeCompare(bv, "pt-BR");
    });
    return u;
  }, [users, sort]);

  function openNew() {
    setEditId(null);
    setNome("");
    setEmail("");
    setSenha("");
    setEditRole(pageRole);
    setModal("edit");
  }

  function openEdit(u: User) {
    setEditId(u.id);
    setNome(u.name);
    setEmail(u.email);
    setSenha("");
    setEditRole(u.role);
    setModal("edit");
  }

  async function save(e: FormEvent) {
    e.preventDefault();
    if (!token) return;
    try {
      if (editId) {
        await apiJson(`/users/${editId}`, {
          method: "PUT",
          token,
          body: JSON.stringify({ name: nome, email, role: editRole }),
        });
      } else {
        if (!senha || senha.length < 6) {
          setError("Senha mínimo 6 caracteres");
          return;
        }
        await apiJson("/users", {
          method: "POST",
          token,
          body: JSON.stringify({ name: nome, email, password: senha, role: pageRole }),
        });
      }
      setModal("none");
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  async function confirmDelete(ev: FormEvent) {
    ev.preventDefault();
    if (!token || !delId) return;
    try {
      await apiVoid(`/users/${delId}`, { method: "DELETE", token });
      setModal("none");
      setDelId(null);
      await load();
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
    }
  }

  if (!ready || !token || !isAdmin) return null;
  if (users === null) return <p style={{ padding: 24, color: "#888" }}>Carregando…</p>;

  return (
    <>
      <div className="dashboard-container">
        <div className="content-card">
          <h3>{c.title}</h3>
          {error ? <div className="alert-legacy-error">{error}</div> : null}
          <div className="page-controls">
            <div className="sort-section">
              <label htmlFor="ordenar">{t.projects.orderBy}</label>
              <select
                id="ordenar"
                className="button-secondary"
                style={{ padding: 8 }}
                value={sort}
                onChange={(e) => setSort(e.target.value as "nome" | "email")}
              >
                <option value="nome">Nome (A-Z)</option>
                <option value="email">E-mail (A-Z)</option>
              </select>
            </div>
            <button type="button" className="button" onClick={openNew}>
              {c.newBtn}
            </button>
          </div>
          <table id="tabelaUsuarios">
            <thead>
              <tr>
                <th>{t.admins.tableName}</th>
                <th>{t.admins.tableEmail}</th>
                <th>{t.admins.tableActions}</th>
              </tr>
            </thead>
            <tbody>
              {sorted.map((u) => (
                <tr key={u.id}>
                  <td>{u.name}</td>
                  <td>{u.email}</td>
                  <td className="actions-cell">
                    <button type="button" title="Editar" onClick={() => openEdit(u)}>
                      <i className="fas fa-pen" />
                    </button>
                    <button
                      type="button"
                      title="Excluir"
                      onClick={() => {
                        setDelId(u.id);
                        setModal("delete");
                      }}
                    >
                      <i className="fas fa-trash" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {sorted.length === 0 ? (
            <p style={{ textAlign: "center", color: "#888", padding: 20 }}>Nenhum registro.</p>
          ) : null}
          <div style={{ marginTop: 16 }}>
            <Link href="/" className="button-secondary" style={{ padding: "10px 16px" }}>
              Home
            </Link>
          </div>
        </div>
      </div>

      <div className={modal === "edit" ? "modal show" : "modal"}>
        <div className="modal-content">
          <span className="close" onClick={() => setModal("none")}>
            &times;
          </span>
          <h2>{editId ? "Editar" : "Novo"}</h2>
          <form onSubmit={save}>
            <div className="form-group">
              <label>Nome</label>
              <input value={nome} onChange={(e) => setNome(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>E-mail</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Senha</label>
              <input
                type="password"
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                minLength={editId ? 0 : 6}
                required={!editId}
              />
              <p className="password-hint">{t.admins.passwordTip}</p>
            </div>
            <div className="form-actions" style={{ justifyContent: "center" }}>
              <input type="submit" value={t.admins.save} className="button" />
            </div>
          </form>
        </div>
      </div>

      <div className={modal === "delete" ? "modal show" : "modal"}>
        <div className="modal-content" style={{ textAlign: "center" }}>
          <span className="close" onClick={() => setModal("none")}>
            &times;
          </span>
          <h2>{t.admins.deleteTitle}</h2>
          <p>Confirma exclusão?</p>
          <form onSubmit={confirmDelete}>
            <div className="form-actions">
              <button type="button" className="button-secondary" onClick={() => setModal("none")}>
                Cancelar
              </button>
              <input type="submit" value="Excluir" className="button" style={{ background: "#dc143c" }} />
            </div>
          </form>
        </div>
      </div>
    </>
  );
}
