import type { Metadata } from "next";
import { AuthProvider } from "@/context/auth-context";
import { LegacyHeader } from "@/components/LegacyHeader";
import { LegacyFooter } from "@/components/LegacyFooter";
import "./globals.css";

export const metadata: Metadata = {
  title: "GameTests Platform",
  description: "Sistema de Gerenciamento de Testes",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <head>
        <link
          rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"
        />
      </head>
      <body>
        <AuthProvider>
          <LegacyHeader />
          {children}
          <LegacyFooter />
        </AuthProvider>
      </body>
    </html>
  );
}
