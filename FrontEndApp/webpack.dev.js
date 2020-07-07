const path = require('path');
const common = require('./webpack.config.js');
const merge = require('webpack-merge');
const BACKEND = 'http://localhost:3000';

module.exports = merge(common, {
    mode:'development',
    devtool: 'inline-source-map',
    
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },
    devServer: {
        clientLogLevel: 'warning',
        historyApiFallback: {
            rewrites: [
                {
                    from: /.*/, to: '/'
                },
            ],
        },
        contentBase: false,
        compress: true,
        host: '0.0.0.0',
        port: 8080,
        overlay: {
            warnings: false,
            errors: true
        },
        publicPath: '/',
        proxy: {
            '/api': {
                target: BACKEND,
                secure: false,
                changeOrigin: true
            }
        },
        watchOptions: {
          poll: false,
        },
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
          "Access-Control-Allow-Headers": "X-Requested-With, content-type, Authorization"
        }
    }
});