const getErrorMessage = async (response: Response) => {
  const text = await response.text();

  if (text) return text;
  return `Request failed with status ${response.status}`;
};

export class RequestError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "RequestError";
    this.status = status;
  }
}

export const request = async <T>(input: RequestInfo | URL, init?: RequestInit): Promise<T> => {
  const response = await fetch(
    input,
    {
      credentials: "same-origin",
      ...init,
    }
  );

  if (!response.ok) {
    throw new RequestError(await getErrorMessage(response), response.status);
  }

  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    return await response.json();
  }

  const text = await response.text();

  if (text.length === 0) {
    return undefined as T;
  }

  throw new Error("Unknown response object");
};
