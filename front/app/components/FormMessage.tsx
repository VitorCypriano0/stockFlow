interface FormMessageProps {
  texto: string;
}

export default function FormMessage({ texto }: FormMessageProps) {
  return (
    <p
      role="alert"
      className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
    >
      {texto}
    </p>
  );
}
