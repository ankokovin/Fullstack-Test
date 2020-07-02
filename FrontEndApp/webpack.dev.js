const path = require('path');
const common = require('./webpack.config.js');
const merge = require('webpack-merge');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

module.exports = merge(common, {
    mode:'development',
    devtool: 'inline-source-map',
    entry:{
        path: path.resolve(__dirname, 'src/app.js')
    },
    plugins: [
       // new CleanWebpackPlugin(),
    ],
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist'),
    },
});