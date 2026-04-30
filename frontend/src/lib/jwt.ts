function decodePayload(segment: string): string {
  let base64 = segment.replace(/-/g, "+").replace(/_/g, "/");
  const pad = base64.length % 4;
  if (pad) base64 += "=".repeat(4 - pad);
  return decodeURIComponent(
    Array.from(atob(base64), (c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2)).join("")
  );
}

export function parseJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const raw = token.replace(/^Bearer\s+/i, "");
    const segment = raw.split(".")[1];
    if (!segment) return null;
    const json = decodePayload(segment);
    return JSON.parse(json) as Record<string, unknown>;
  } catch {
    return null;
  }
}

export function tokenIsAdmin(token: string | null): boolean {
  if (!token) return false;
  const payload = parseJwtPayload(token);
  const scope = typeof payload?.scope === "string" ? payload.scope : "";
  return scope.split(/\s+/).includes("ROLE_ADMIN");
}

export function tokenSubjectId(token: string | null): string | null {
  if (!token) return null;
  const payload = parseJwtPayload(token);
  const sub = payload?.sub;
  return typeof sub === "string" ? sub : null;
}
