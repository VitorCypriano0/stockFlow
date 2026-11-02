"use client";

import { useEffect, useState, type ReactNode } from "react";
import { Provider } from "react-redux";
import { makeStore } from "./store";
import { verificacaoConcluida } from "./slices/authSlice";
import { conectarStore } from "@/app/services/api";

export default function StoreProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [store] = useState(makeStore);

  useEffect(() => {
    conectarStore(store);
    store.dispatch(verificacaoConcluida());
    return () => conectarStore(null);
  }, [store]);

  return <Provider store={store}>{children}</Provider>;
}
