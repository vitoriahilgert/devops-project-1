"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/auth-context";
import { apiMultipart, ApiError } from "@/lib/api";
import { t } from "@/lib/strings";

export default function CreateStrategyPage() {
  const router = useRouter();
  const { token, isAdmin, ready } = useAuth();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [examples, setExamples] = useState("");
  const [tips, setTips] = useState("");
  const [files, setFiles] = useState<FileList | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!ready) return;
    if (!token || !isAdmin) router.replace("/");
  }, [ready, token, isAdmin, router]);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    if (!token) return;
    setError(null);
    const fd = new FormData();
    const strategyJson = JSON.stringify({ name, description, examples, tips });
    fd.append("strategy", new Blob([strategyJson], { type: "application/json" }));
    if (files?.length) {
      Array.from(files).forEach((f) => fd.append("images", f));
    }
    try {
      await apiMultipart("/strategies", fd, token);
      router.push("/strategies");
    } catch (err) {
      if (err instanceof ApiError) setError(err.body);
      else setError("Erro");
    }
  }

  if (!ready || !token || !isAdmin) return null;

  return (
    <div className="legacy-container">
      <h1>{t.strategies.createTitle}</h1>
      <Link href="/strategies">
        <button type="button" className="btn-create">
          {t.strategies.createBack}
        </button>
      </Link>
      {error ? <div className="alert-legacy-error">{error}</div> : null}
      <form onSubmit={onSubmit} style={{ marginTop: 20 }}>
        <label>{t.strategies.createName}</label>
        <input value={name} onChange={(e) => setName(e.target.value)} required />
        <label>{t.strategies.createDesc}</label>
        <textarea value={description} onChange={(e) => setDescription(e.target.value)} required rows={4} />
        <label>{t.strategies.createEx}</label>
        <textarea value={examples} onChange={(e) => setExamples(e.target.value)} rows={4} />
        <label>{t.strategies.createTip}</label>
        <textarea value={tips} onChange={(e) => setTips(e.target.value)} rows={4} />
        <label>{t.strategies.createImages}</label>
        <input type="file" accept="image/*" multiple onChange={(e) => setFiles(e.target.files)} />
        <div style={{ marginTop: 20 }}>
          <input type="submit" value={t.strategies.createSubmit} />
        </div>
      </form>
    </div>
  );
}
