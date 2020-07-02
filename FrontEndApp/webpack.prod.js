const merge = require('webpack-merge');
const common = require('./webpack.config.js');
const path = require('path');

module.exports = merge(common, {
    mode: 'production',
    devtool: "source-map",
    output: {
        filename: '[name].[hash].js',
        path: path.resolve(__dirname, '../WebServer/static')
    }
});