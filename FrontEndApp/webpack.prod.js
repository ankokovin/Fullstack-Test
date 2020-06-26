const merge = require('webpack-merge');
const webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
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