const fs = require('fs');
const path = require('path');

function processFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    const ext = path.extname(filePath);
    
    let original = content;
    if (ext === '.java' || ext === '.js' || ext === '.css') {
        // Remove block comments
        content = content.replace(/\/\*[\s\S]*?\*\//g, '');
        // Remove single line comments that are on their own line
        content = content.replace(/^\s*\/\/.*$/gm, '');
    } else if (ext === '.html') {
        // Remove HTML comments
        content = content.replace(/<!--[\s\S]*?-->/g, '');
    } else if (ext === '.properties') {
        // Remove property comments
        content = content.replace(/^\s*#.*$/gm, '');
    }
    
    // Clean up multiple empty lines
    content = content.replace(/\n\s*\n\s*\n/g, '\n\n');

    if (original !== content) {
        fs.writeFileSync(filePath, content, 'utf8');
        return true;
    }
    return false;
}

function walkDir(dir) {
    let modified = 0;
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            modified += walkDir(fullPath);
        } else {
            if (['.java', '.html', '.css', '.js', '.properties'].includes(path.extname(fullPath))) {
                if (processFile(fullPath)) modified++;
            }
        }
    }
    return modified;
}

const mod = walkDir('c:/Users/ccagl/Desktop/tinyhouse/src/main');
console.log('Modified ' + mod + ' files');
