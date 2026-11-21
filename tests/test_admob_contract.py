"""Ensure native Android AdMob configuration matches Universal Build's JSON contract."""
import json
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CONFIG = ROOT / "config" / "universal-build-admob.json"


class AdMobContractTest(unittest.TestCase):
    def test_contract_v1(self):
        data = json.loads(CONFIG.read_text(encoding="utf-8"))
        self.assertEqual(data["version"], 1)
        self.assertIn(data["mode"], {"off", "test", "live"})
        self.assertEqual(set(data["enabled"]), {"banner", "interstitial", "rewarded"})
        self.assertEqual(set(data["units"]), {"banner", "interstitial", "rewarded"})
        self.assertTrue(all(isinstance(value, bool) for value in data["enabled"].values()))
        self.assertTrue(all(isinstance(value, str) for value in data["units"].values()))
        self.assertTrue(30 <= data["interstitialCooldownSeconds"] <= 3600)
        self.assertIsInstance(data["appId"], str)

    def test_app_reads_contract_and_uses_ads_module(self):
        gradle = (ROOT / "app" / "build.gradle.kts").read_text(encoding="utf-8")
        settings = (ROOT / "settings.gradle.kts").read_text(encoding="utf-8")
        self.assertIn('config/universal-build-admob.json', gradle)
        self.assertIn('implementation(project(":core:ads"))', gradle)
        self.assertIn('include(":core:ads")', settings)
        self.assertIn("configureUniversalAds(this, false)", gradle)
        self.assertIn("configureUniversalAds(this, true)", gradle)


if __name__ == "__main__":
    unittest.main()
