import type { ReactNode } from "react";
import AuthGuard from "../components/AuthGuard";
import Footer from "../components/Footer";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";

export default function SistemaLayout({
  children,
}: Readonly<{ children: ReactNode }>) {
  return (
    <AuthGuard>
      <div className="min-h-screen bg-slate-50 md:flex">
        <Sidebar />
        <div className="flex min-h-screen min-w-0 flex-1 flex-col">
          <Header />
          <main className="flex-1 px-4 py-7 sm:px-8 sm:py-9">{children}</main>
          <Footer />
        </div>
      </div>
    </AuthGuard>
  );
}
