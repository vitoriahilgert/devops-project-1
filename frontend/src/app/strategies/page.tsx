"use client";

import { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/context/auth-context";
import { apiJson, apiVoid, ApiError } from "@/lib/api";
import { assetUrl } from "@/lib/config";
import type { Strategy } from "@/lib/types";
import { t } from "@/lib/strings";

export default function StrategiesPage() {
  const { token, isAdmin } = useAuth();
  const [strategies, setStrategies] = useState<Strategy[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [galleryUrls, setGalleryUrls] = useState<string[] | null>(null);

  const load = useCallback(async () => {
    try {
      setStrategies(await apiJson<Strategy[]>("/strategies", { token: null }));
      setError(null);
    } catch (e) {
      if (e instanceof ApiError) setError(e.body);
      else setError("Erro");
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  async function removeStrategy(id: string) {
    if (!token || !confirm("Excluir estratégia?")) return;
    try {
      await apiVoid(`/strategies/${id}`, { method: "DELETE", token });
      await load();
    } catch (e) {
      if (e instanceof ApiError) setError(e.body);
    }
  }

  if (strategies === null) return <p style={{ padding: 24, color: "#888" }}>Carregando…</p>;

  return (
    <>
      {error ? (
        <div className="alert-legacy-error" style={{ margin: "0 5% 12px" }}>
          {error}
        </div>
      ) : null}
      <div className="legacy-container wide">
        <div className="strategies-header">
          <Link href="/">
            <button type="button" className="btn-create">
              {t.strategies.back}
            </button>
          </Link>
          <h1>{t.strategies.listTitle}</h1>
          {isAdmin ? (
            <Link href="/strategies/create">
              <button type="button" className="btn-create">
                {t.strategies.create}
              </button>
            </Link>
          ) : (
            <span />
          )}
        </div>

        <table>
          <thead>
            <tr>
              <th>{t.strategies.tableName}</th>
              <th>{t.strategies.tableDesc}</th>
              <th>{t.strategies.tableEx}</th>
              <th>{t.strategies.tableTip}</th>
              <th>{t.strategies.tableImg}</th>
              {isAdmin ? <th>{t.strategies.tableDel}</th> : null}
            </tr>
          </thead>
          <tbody>
            {strategies.length === 0 ? (
              <tr>
                <td colSpan={isAdmin ? 6 : 5} style={{ color: "#888" }}>
                  {t.strategies.empty}
                </td>
              </tr>
            ) : (
              strategies.map((st) => (
                <tr key={st.id}>
                  <td>{st.name}</td>
                  <td>{st.description}</td>
                  <td style={{ maxWidth: 220, fontSize: "0.85em" }}>{st.examples}</td>
                  <td style={{ maxWidth: 220, fontSize: "0.85em" }}>{st.tips}</td>
                  <td className="actions">
                    {st.imageUrls?.length ? (
                      <button
                        type="button"
                        title={t.strategies.gallery}
                        className="btn-action"
                        onClick={() => setGalleryUrls(st.imageUrls ?? [])}
                      >
                        <i className="fas fa-images" />
                      </button>
                    ) : (
                      <span style={{ color: "#666" }}>—</span>
                    )}
                  </td>
                  {isAdmin ? (
                    <td className="actions">
                      <button
                        type="button"
                        title={t.strategies.tableDel}
                        className="btn-action"
                        onClick={() => removeStrategy(st.id)}
                      >
                        <i className="fas fa-trash" />
                      </button>
                    </td>
                  ) : null}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className={galleryUrls ? "modal show" : "modal"} onClick={() => setGalleryUrls(null)}>
        <div className="modal-content gallery-modal-content" onClick={(e) => e.stopPropagation()}>
          <span className="close" onClick={() => setGalleryUrls(null)}>
            &times;
          </span>
          <h2>{t.strategies.gallery}</h2>
          <div className="image-gallery">
            {(galleryUrls ?? []).map((url) => (
              <a key={url} href={assetUrl(url)} target="_blank" rel="noreferrer">
                <img src={assetUrl(url)} alt="" />
              </a>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}
