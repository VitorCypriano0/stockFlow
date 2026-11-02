import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { buscarDadosDashboard, type DadosDashboard } from "@/app/services/dashboardService";
import { mensagemDeErro } from "@/app/services/api";
import { logout } from "./authSlice";

interface DashboardState extends DadosDashboard {
  estado: "ocioso" | "carregando" | "carregado" | "falhou";
  erro: string | null;
}

const estadoInicial: DashboardState = {
  resumo: {
    itens: 0,
    fornecedores: 0,
    estoqueBaixo: 0,
    unidades: 0,
    movimentacoesHoje: 0,
  },
  recentes: [],
  estado: "ocioso",
  erro: null,
};

export const carregarDashboard = createAsyncThunk<
  DadosDashboard,
  void,
  { rejectValue: string }
>("dashboard/carregar", async (_, { rejectWithValue }) => {
  try {
    return await buscarDadosDashboard();
  } catch (erro) {
    return rejectWithValue(mensagemDeErro(erro, "Não foi possível carregar a visão geral."));
  }
});

const dashboardSlice = createSlice({
  name: "dashboard",
  initialState: estadoInicial,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(carregarDashboard.pending, (state) => {
        state.estado = "carregando";
        state.erro = null;
      })
      .addCase(carregarDashboard.fulfilled, (state, action) => {
        state.resumo = action.payload.resumo;
        state.recentes = action.payload.recentes;
        state.estado = "carregado";
      })
      .addCase(carregarDashboard.rejected, (state, action) => {
        state.estado = "falhou";
        state.erro = action.payload ?? "Não foi possível carregar a visão geral.";
      })
      .addCase(logout, () => estadoInicial);
  },
});

export default dashboardSlice.reducer;
