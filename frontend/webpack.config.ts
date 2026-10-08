import path from 'path';
import webpack from 'webpack';

import { BuildOptions, buildWebpack } from './build-config';

type EnvVariables = {}

module.exports = (env: EnvVariables, argv: BuildOptions) => {
  const config: webpack.Configuration = buildWebpack({
    mode: argv.mode || 'development',
    port: 3000,
    paths: {
      entry: path.resolve(__dirname, 'src/index'),
      html: path.resolve(__dirname, 'public/index.html'),
      output: path.resolve(__dirname, 'dist'),
      src: path.resolve(__dirname, 'src'),
      public: path.resolve(__dirname, 'public'),
    },
    analyzer: false,
  });

  return config;
};

