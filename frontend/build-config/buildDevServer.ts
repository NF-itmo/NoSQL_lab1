import type { Configuration as DevServerConfiguration } from "webpack-dev-server";
import {BuildOptions} from "./types/types";

export function buildDevServer(options: BuildOptions): DevServerConfiguration {
    const backendTarget = process.env.BACKEND_PROXY_TARGET || 'http://localhost:8080';
    const backendRoot = "/api"

    return {
        allowedHosts: 'all',
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': '*',
          'Access-Control-Allow-Headers': '*',
        },
        compress: true,
        host: '0.0.0.0',
        port: options.port ?? 3000,
        open: false,
        historyApiFallback: true,
        hot: true,
        proxy: [
            {
                context: backendRoot,
                target: backendTarget,
                changeOrigin: true,
                secure: false,
            },
        ],
    }
}
