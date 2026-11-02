import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { Almoxarife } from "@/app/types/stockflow";

export interface AuthState {
  token: string | null;
  usuario: Almoxarife | null;
  estado: "verificando" | "autenticado" | "naoAutenticado";
}

const estadoInicial: AuthState = {
  token: null,
  usuario: null,
  estado: "verificando",
};

const authSlice = createSlice({
  name: "auth",
  initialState: estadoInicial,
  reducers: {
    login(state, action: PayloadAction<{ token: string; usuario: Almoxarife }>) {
      state.token = action.payload.token;
      state.usuario = action.payload.usuario;
      state.estado = "autenticado";
    },
    logout(state) {
      state.token = null;
      state.usuario = null;
      state.estado = "naoAutenticado";
    },
    verificacaoConcluida(state) {
      if (state.estado === "verificando") {
        state.estado = "naoAutenticado";
      }
    },
  },
});

export const { login, logout, verificacaoConcluida } = authSlice.actions;
export default authSlice.reducer;
