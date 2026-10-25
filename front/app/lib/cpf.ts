function calcularDigito(cpf: string, quantidade: number, pesoInicial: number): number {
  let soma = 0;

  for (let indice = 0; indice < quantidade; indice++) {
    soma += Number(cpf[indice]) * (pesoInicial - indice);
  }

  const digito = 11 - (soma % 11);
  return digito > 9 ? 0 : digito;
}

export function cpfValido(cpf: string): boolean {
  if (!/^[0-9.\s-]+$/.test(cpf)) {
    return false;
  }

  const digitos = cpf.replace(/\D/g, "");
  if (digitos.length !== 11 || /^([0-9])\1{10}$/.test(digitos)) {
    return false;
  }

  const primeiroDigito = calcularDigito(digitos, 9, 10);
  if (primeiroDigito !== Number(digitos[9])) {
    return false;
  }

  const segundoDigito = calcularDigito(digitos, 10, 11);
  return segundoDigito === Number(digitos[10]);
}
