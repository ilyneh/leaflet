"""Prints a markdown summary of JUnit test failures for $GITHUB_STEP_SUMMARY."""
import glob
import xml.etree.ElementTree as ET

print("## Failing tests\n")
any_failures = False
for path in sorted(glob.glob("**/build/test-results/**/TEST-*.xml", recursive=True)):
    suite = ET.parse(path).getroot()
    for case in suite.iter("testcase"):
        for kind in ("failure", "error"):
            node = case.find(kind)
            if node is None:
                continue
            any_failures = True
            print(f"### `{case.get('classname')}` > {case.get('name')}")
            print(f"**{node.get('message', kind)}**\n")
            print("```")
            print((node.text or "").strip()[:3000])
            print("```\n")
if not any_failures:
    print("No JUnit failures found — the build failed before or outside test execution.")
