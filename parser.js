const fs = require('fs');
let sql = fs.readFileSync('backup.sql', 'utf8');
let inserts = sql.match(/INSERT INTO "PUBLIC"\."\w+" VALUES[\s\S]*?;/g);
if (inserts) {
    // lowercase the table names and remove PUBLIC schema
    inserts = inserts.map(s => s.replace(/"PUBLIC"\."(\w+)"/g, (match, p1) => p1.toLowerCase()));
    
    // some enum columns in H2 dump might be quoted or have weird formats, but let's assume they are fine for PG
    fs.writeFileSync('pg_inserts.sql', inserts.join('\n\n'));
    console.log('Extracted ' + inserts.length + ' statements');
} else {
    console.log('No inserts found');
}
