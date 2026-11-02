
import type { Metadata } from "next";
import type { ReactNode } from "react";
import StoreProvider from "@/app/redux/StoreProvider";
import "./globals.css";

export const metadata: Metadata = {
  title: "Stock Flow",
  description: "Controle de almoxarifado e movimentações de estoque",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: ReactNode;
}>) {
  return (
    <html lang="pt-BR">
      <body>
        <StoreProvider>{children}</StoreProvider>
      </body>
    </html>
  );
}
