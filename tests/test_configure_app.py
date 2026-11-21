import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "tools"))
from configure_app import apply_updates


class AppConfigTests(unittest.TestCase):
    def test_change_identity_without_touching_sdk(self):
        old = "appName=Old\napplicationId=br.com.old.app\ntargetSdk=36\n"
        new = apply_updates(old, {"appName": "Test App", "applicationId": "br.com.new.app"})
        self.assertIn("appName=Test App", new)
        self.assertIn("applicationId=br.com.new.app", new)
        self.assertIn("targetSdk=36", new)

    def test_reject_insecure_network(self):
        with self.assertRaises(ValueError):
            apply_updates("", {"baseApiUrl": "http://insecure.example/"})

    def test_reject_invalid_package(self):
        with self.assertRaises(ValueError):
            apply_updates("", {"applicationId": "bad app ID"})

    def test_reject_newline_injection(self):
        with self.assertRaises(ValueError):
            apply_updates("", {"appName": "Test\npassword=secret"})


if __name__ == "__main__":
    unittest.main()
