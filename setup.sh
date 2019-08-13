#! /bin/sh
  
echo "Installing npm packages for conventional commit message preparation"
npm install

# copy github hook

echo "Copying git hooks"

mkdir -p .git/hooks

cp githooks/commit-msg.hook .git/hooks/commit-msg

chmod +x .git/hooks/commit-msg

echo "Setting commit message template"

git config commit.template COMMIT_MSG_TEMPLATE

