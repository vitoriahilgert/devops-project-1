export function getApiBase(): string {
  if (process.env.NODE_ENV === "development") {
    return (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080").replace(/\/$/, "");
  }
  return "/api";
}

export function assetUrl(path: string): string {
  if (!path) return "";
  if (path.startsWith("http://") || path.startsWith("https://")) return path;
  const p = path.startsWith("/") ? path : `/${path}`;
  if (process.env.NODE_ENV === "development") {
    const base = (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080").replace(/\/$/, "");
    return `${base}${p}`;
  }
  return p;
}
