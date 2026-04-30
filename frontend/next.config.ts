import type { NextConfig } from "next";
import path from "node:path";

const nextConfig: NextConfig = {
  output: "standalone",
  // Com package-lock na raiz do monorepo, sem isso o standalone vira .next/standalone/frontend/ e quebra o Dockerfile.
  outputFileTracingRoot: path.join(__dirname),
};

export default nextConfig;
