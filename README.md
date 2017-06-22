Or and ben - android final project repo.

create a new repository on the command line:

echo "# new-repo" >> README.md
git init
git add README.md
git commit -m "first commit"
git remote add origin https://github.com/mosheor/new-repo.git
git push -u origin master


push an existing repository from the command line:

git remote add origin https://github.com/mosheor/new-repo.git
git push -u origin master
