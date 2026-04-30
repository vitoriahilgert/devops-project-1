"use client";

import { t } from "@/lib/strings";

export function LegacyFooter() {
  const year = new Date().getFullYear();
  return (
    <footer className="page-footer">
      © {year} {t.footer}
    </footer>
  );
}
